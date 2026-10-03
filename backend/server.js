const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '.env') });
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const fs = require('fs');
const connectDB = require('./config/db');
const aiRoutes = require('./routes/aiRoutes');
const eventRoutes = require('./routes/eventRoutes');
const { getCategories } = require('./controllers/eventController');
const Event = require('./models/Event');

const app = express();

app.use(helmet());
app.use(cors());
app.use(express.json());

connectDB().then(async () => {
    try {
        const count = await Event.countDocuments();
        if (count === 0) {
            const eventsData = JSON.parse(fs.readFileSync(path.join(__dirname, 'data', 'events.json'), 'utf-8'));
            await Event.insertMany(eventsData);
            console.log(`Successfully seeded ${eventsData.length} mock events to database with tags`);
        }
    } catch (err) {
        console.error('Error seeding database:', err);
    }
});

app.use('/api/ai', aiRoutes);
app.use('/api/events', eventRoutes);
app.get('/api/categories', getCategories);

app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({ error: 'Internal Server Error' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
