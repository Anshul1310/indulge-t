const Event = require('../models/Event');
const {
    detectMoodAndCity,
    verifyEventsWithAi,
    MOOD_TAG_MAP
} = require('../services/aiService');
const {
    extractPreliminaryConstraints,
    rankEvents
} = require('../services/eventService');

function extractPromptKeywords(prompt) {
    const commonKeywords = [
        'laughter', 'laugh', 'comedy', 'standup', 'improv', 'humor', 'jokes',
        'jazz', 'acoustic', 'techno', 'edm', 'rock', 'bollywood', 'retro',
        'pottery', 'art', 'painting', 'craft',
        'yoga', 'meditation', 'wellness', 'trekking', 'kayaking', 'cycling',
        'coffee', 'wine', 'beer', 'foodie', 'pizza',
        'board game', 'games', 'arcade', 'karaoke', 'dance', 'party', 'books', 'reading'
    ];
    const lower = prompt.toLowerCase();
    return commonKeywords.filter(k => lower.includes(k));
}

async function recommend(req, res) {
    try {
        const { prompt } = req.body;
        if (!prompt) {
            return res.status(400).json({ error: 'Prompt is required' });
        }

        const priceConstraints = extractPreliminaryConstraints(prompt);
        const { minPrice, maxPrice } = priceConstraints;

        const { mood, city: detectedCity } = await detectMoodAndCity(prompt);
        const city = priceConstraints.city || detectedCity || null;

        const moodTags = mood && MOOD_TAG_MAP[mood] ? MOOD_TAG_MAP[mood] : [];
        const promptKeywords = extractPromptKeywords(prompt);
        const allMatchedTags = Array.from(new Set([...moodTags, ...promptKeywords]));

        const preferences = {
            minPrice,
            maxPrice,
            mood,
            city,
            matchedTags: allMatchedTags
        };

        const allEvents = await Event.find({}).lean();
        if (allEvents.length === 0) {
            return res.status(200).json({
                message: 'No events are currently available.',
                data: [],
                preferences
            });
        }

        const rankedCandidates = rankEvents(allEvents, preferences);
        const topCandidates = rankedCandidates.slice(0, 6);

        const { selectedIds, message } = await verifyEventsWithAi(prompt, topCandidates, preferences);

        let finalEvents = selectedIds
            .map(id => topCandidates.find(e => String(e.id || e._id) === id))
            .filter(Boolean);

        if (finalEvents.length === 0) {
            finalEvents = topCandidates.slice(0, 3);
        }

        return res.status(200).json({
            message,
            data: finalEvents,
            preferences
        });

    } catch (error) {
        return res.status(500).json({ error: error.message || 'Failed to process recommendation' });
    }
}

module.exports = {
    recommend
};
