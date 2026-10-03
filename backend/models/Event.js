const mongoose = require('mongoose');

const eventSchema = new mongoose.Schema({
    id: { type: String, required: true },
    name: { type: String, required: true },
    category: { type: String, required: true },
    mood: { type: String, required: true },
    city: { type: String, required: true },
    price: { type: Number, required: true },
    date: { type: String, required: true },
    description: { type: String, required: true },
    tags: [{ type: String, trim: true, lowercase: true }]
}, { timestamps: true });

module.exports = mongoose.model('Event', eventSchema);
