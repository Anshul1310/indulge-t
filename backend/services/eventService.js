const Event = require('../models/Event');

async function getAllUniqueTags() {
    try {
        const tags = await Event.distinct('tags');
        return tags.filter(Boolean);
    } catch {
        return [];
    }
}

function extractPreliminaryConstraints(prompt) {
    const lower = prompt.toLowerCase();
    let minPrice = null;
    let maxPrice = null;
    let city = null;

    const cities = ['mumbai', 'bangalore', 'delhi', 'goa', 'pune', 'hyderabad', 'chennai', 'kolkata'];
    for (const c of cities) {
        if (lower.includes(c)) {
            city = c.charAt(0).toUpperCase() + c.slice(1);
            break;
        }
    }

    const rangeMatch = lower.match(/(?:between|from)?\s*(?:rs\.?|inr|₹)?\s*(\d{2,6})\s*(?:and|to|-|–|&)\s*(?:rs\.?|inr|₹)?\s*(\d{2,6})/i);
    if (rangeMatch) {
        minPrice = parseInt(rangeMatch[1], 10);
        maxPrice = parseInt(rangeMatch[2], 10);
        if (minPrice > maxPrice) {
            const temp = minPrice;
            minPrice = maxPrice;
            maxPrice = temp;
        }
    } else {
        const aboveMatch = lower.match(/(?:above|over|more than|greater than|min(?:imum)?|at least)\s*(?:rs\.?|inr|₹)?\s*(\d{2,6})/i);
        if (aboveMatch) minPrice = parseInt(aboveMatch[1], 10);

        const underMatch = lower.match(/(?:under|below|less than|within|max(?:imum)?|budget(?: of)?)\s*(?:rs\.?|inr|₹)?\s*(\d{2,6})/i);
        if (underMatch) maxPrice = parseInt(underMatch[1], 10);
    }

    return { minPrice, maxPrice, city };
}

function rankEvents(events, preferences) {
    const matchedTagsSet = new Set((preferences.matchedTags || []).map(t => String(t).toLowerCase().trim()));
    const targetCity = preferences.city ? preferences.city.toLowerCase() : null;
    const targetMood = preferences.mood ? preferences.mood.toLowerCase() : null;

    const scoredEvents = events.map(event => {
        let score = 0;

        if (targetMood) {
            const eventMood = (event.mood || '').toLowerCase();
            if (eventMood === targetMood) {
                score += 50;
            } else {
                const eventTags = (event.tags || []).map(t => String(t).toLowerCase());
                if (matchedTagsSet.size > 0) {
                    const moodTagMatches = eventTags.filter(t => matchedTagsSet.has(t)).length;
                    if (moodTagMatches > 0) {
                        score += Math.min(35, moodTagMatches * 15);
                    }
                }
            }
        }

        if (targetCity) {
            const eventCity = (event.city || '').toLowerCase();
            if (eventCity === targetCity) {
                score += 25;
            }
        }

        if (matchedTagsSet.size > 0) {
            const eventTags = (event.tags || []).map(t => String(t).toLowerCase());
            let tagMatches = 0;
            eventTags.forEach(t => {
                if (matchedTagsSet.has(t)) tagMatches++;
            });
            score += Math.min(20, tagMatches * 10);
        }

        if (preferences.minPrice !== null || preferences.maxPrice !== null) {
            let inRange = true;
            if (preferences.minPrice !== null && event.price < preferences.minPrice) inRange = false;
            if (preferences.maxPrice !== null && event.price > preferences.maxPrice) inRange = false;
            if (inRange) {
                score += 5;
            }
        }

        return { ...event, score };
    });

    scoredEvents.sort((a, b) => b.score - a.score);
    return scoredEvents;
}

module.exports = {
    getAllUniqueTags,
    extractPreliminaryConstraints,
    rankEvents
};
