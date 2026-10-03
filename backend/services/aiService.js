const { GoogleGenAI } = require('@google/genai');

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });
const MODEL_NAME = 'gemini-3.5-flash-lite';
const AI_TIMEOUT_MS = 7000;

const MOODS = [
    'lonely', 'sad', 'funny', 'angry', 'chill', 'energetic', 'romantic',
    'adventurous', 'creative', 'curious', 'social', 'peaceful', 'bored',
    'happy', 'nostalgic', 'spiritual', 'tired', 'playful', 'excited', 'wild'
];

const CITIES = ['Mumbai', 'Bangalore', 'Delhi', 'Goa', 'Pune', 'Hyderabad', 'Chennai', 'Kolkata'];

const MOOD_TAG_MAP = {
    lonely: ['social', 'hangout', 'board game', 'games', 'friends', 'mixer', 'community', 'interactive'],
    sad: ['comedy', 'laugh', 'chill', 'acoustic', 'relaxing', 'cozy', 'peaceful'],
    funny: ['comedy', 'standup', 'humor', 'laugh', 'laughter', 'hilarious', 'jokes', 'improv', 'comic'],
    angry: ['active', 'fitness', 'workout', 'running', 'calisthenics', 'nature', 'peaceful'],
    chill: ['chill', 'acoustic', 'jazz', 'relaxing', 'cozy', 'rooftop', 'coffee', 'wine', 'lo-fi'],
    energetic: ['dance', 'party', 'techno', 'nightlife', 'active', 'festival', 'club', 'loud'],
    romantic: ['romantic', 'intimate', 'wine', 'jazz', 'candlelight', 'sunset', 'acoustic'],
    adventurous: ['adventure', 'outdoor', 'trekking', 'nature', 'kayaking', 'stargazing', 'active'],
    creative: ['art', 'creative', 'painting', 'pottery', 'workshop', 'craft', 'indie'],
    curious: ['learning', 'tech', 'ai', 'workshop', 'coffee', 'tasting', 'culture', 'heritage'],
    social: ['social', 'networking', 'hangout', 'mixer', 'party', 'founders', 'games'],
    peaceful: ['peaceful', 'meditation', 'yoga', 'wellness', 'nature', 'quiet', 'stargazing', 'lo-fi'],
    bored: ['fun', 'games', 'arcade', 'retro', 'comedy', 'karaoke', 'party', 'interactive'],
    happy: ['fun', 'music', 'dance', 'party', 'foodie', 'social'],
    nostalgic: ['retro', 'vintage', 'bollywood', 'arcade', 'heritage', 'vinyl', 'classic'],
    spiritual: ['spiritual', 'yoga', 'meditation', 'wellness', 'peaceful', 'morning'],
    tired: ['chill', 'relaxing', 'peaceful', 'reading', 'lo-fi', 'cozy', 'coffee'],
    playful: ['games', 'arcade', 'karaoke', 'fun', 'dance', 'comedy'],
    excited: ['party', 'edm', 'rave', 'dance', 'festival', 'adventure', 'loud', 'wild'],
    wild: ['techno', 'rave', 'party', 'nightlife', 'club', 'dance', 'loud']
};

function withTimeout(promise, ms = AI_TIMEOUT_MS) {
    let timer;
    const timeoutPromise = new Promise((_, reject) => {
        timer = setTimeout(() => reject(new Error('AI call timed out')), ms);
    });
    return Promise.race([promise, timeoutPromise]).finally(() => clearTimeout(timer));
}

async function detectMoodAndCity(prompt) {
    let detectedMood = null;
    let detectedCity = null;

    try {
        const systemInstruction = `
        Select at most ONE mood strictly from: ${JSON.stringify(MOODS)} (or null).
        Select at most ONE city from: ${JSON.stringify(CITIES)} (or null).
        Output ONLY JSON: {"mood": "string" | null, "city": "string" | null}
        `;

        const call = ai.models.generateContent({
            model: MODEL_NAME,
            contents: `User query: "${prompt}"`,
            config: {
                systemInstruction,
                responseMimeType: "application/json"
            }
        });

        const response = await withTimeout(call, AI_TIMEOUT_MS);
        const parsed = JSON.parse((response.text || '').replace(/```json/g, '').replace(/```/g, '').trim());

        if (parsed.mood && MOODS.includes(parsed.mood.toLowerCase())) {
            detectedMood = parsed.mood.toLowerCase();
        }
        if (parsed.city && CITIES.map(c => c.toLowerCase()).includes(parsed.city.toLowerCase())) {
            detectedCity = CITIES.find(c => c.toLowerCase() === parsed.city.toLowerCase());
        }
    } catch (err) {
        console.error('AI mood/city detection error:', err.message);
    }

    return { mood: detectedMood, city: detectedCity };
}

async function verifyEventsWithAi(userPrompt, candidateEvents, preferences = {}) {
    if (!candidateEvents || candidateEvents.length === 0) {
        return {
            selectedIds: [],
            message: "I couldn't find any events matching your request."
        };
    }

    try {
        const compactCandidates = candidateEvents.map(e => ({
            id: String(e.id || e._id),
            name: e.name,
            city: e.city,
            price: e.price,
            mood: e.mood,
            category: e.category,
            tags: (e.tags || []).slice(0, 5),
            description: (e.description || '').substring(0, 90)
        }));

        const systemInstruction = `
        User query: "${userPrompt}"
        Preferences: City: ${preferences.city || 'any'}, Mood: ${preferences.mood || 'any'}, Budget: ${preferences.minPrice || 0} - ${preferences.maxPrice || 'any'}.
        Select the best 2 to 4 event IDs matching user intent.
        Return ONLY JSON:
        {"selectedIds": ["id1", "id2"], "message": "Friendly 1-2 sentence response explaining why these match."}
        `;

        const call = ai.models.generateContent({
            model: MODEL_NAME,
            contents: `Candidate events:\n${JSON.stringify(compactCandidates)}`,
            config: {
                systemInstruction,
                responseMimeType: "application/json"
            }
        });

        const response = await withTimeout(call, AI_TIMEOUT_MS);
        const parsed = JSON.parse((response.text || '').replace(/```json/g, '').replace(/```/g, '').trim());
        const selectedIds = Array.isArray(parsed.selectedIds) ? parsed.selectedIds.map(String) : [];

        return {
            selectedIds,
            message: parsed.message || 'Here are the best events matching your vibe:'
        };
    } catch {
        return {
            selectedIds: candidateEvents.slice(0, 3).map(e => String(e.id || e._id)),
            message: 'Here are the top events matching your preferences:'
        };
    }
}

module.exports = {
    MOODS,
    CITIES,
    MOOD_TAG_MAP,
    detectMoodAndCity,
    verifyEventsWithAi
};
