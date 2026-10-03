const express = require('express');
const router = express.Router();
const {
    getEvents,
    getTags,
    getEventById
} = require('../controllers/eventController');

router.get('/', getEvents);
router.get('/tags', getTags);
router.get('/:id', getEventById);

module.exports = router;
