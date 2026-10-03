const mongoose = require('mongoose');
const Event = require('../models/Event');
const { getAllUniqueTags } = require('../services/eventService');

async function getEvents(req, res) {
    try {
        const { tag, category, city, search } = req.query;
        const filter = {};

        if (tag && tag.trim()) {
            filter.tags = tag.trim().toLowerCase();
        }

        if (category && category.trim() && category !== 'All') {
            filter.category = new RegExp(`^${category.trim()}$`, 'i');
        }

        if (city && city.trim() && city !== 'All') {
            filter.city = new RegExp(`^${city.trim()}$`, 'i');
        }

        if (search && search.trim()) {
            filter.$or = [
                { name: new RegExp(search.trim(), 'i') },
                { description: new RegExp(search.trim(), 'i') },
                { tags: new RegExp(search.trim(), 'i') }
            ];
        }

        const events = await Event.find(filter).sort({ createdAt: -1 }).lean();
        return res.status(200).json({
            success: true,
            count: events.length,
            data: events
        });
    } catch (error) {
        return res.status(500).json({ error: 'Failed to fetch events' });
    }
}

async function getEventById(req, res) {
    try {
        const { id } = req.params;
        let event = null;

        if (mongoose.Types.ObjectId.isValid(id)) {
            event = await Event.findById(id).lean();
        }
        if (!event) {
            event = await Event.findOne({ id }).lean();
        }
        if (!event) {
            return res.status(404).json({ error: 'Event not found' });
        }

        return res.status(200).json({
            success: true,
            data: event
        });
    } catch (error) {
        return res.status(500).json({ error: 'Failed to fetch event details' });
    }
}

async function getCategories(req, res) {
    try {
        const categories = await Event.distinct('category');
        return res.status(200).json({
            success: true,
            data: categories.filter(Boolean)
        });
    } catch (error) {
        return res.status(500).json({ error: 'Failed to fetch categories' });
    }
}

async function getTags(req, res) {
    try {
        const tags = await getAllUniqueTags();
        return res.status(200).json({
            success: true,
            data: tags
        });
    } catch (error) {
        return res.status(500).json({ error: 'Failed to fetch tags' });
    }
}

module.exports = {
    getEvents,
    getEventById,
    getCategories,
    getTags
};
