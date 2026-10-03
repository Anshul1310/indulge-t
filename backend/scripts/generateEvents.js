const fs = require('fs');
const path = require('path');

const cities = ['Delhi', 'Mumbai', 'Bangalore', 'Pune', 'Hyderabad', 'Chennai'];
const categories = ['Fitness', 'Music', 'Social Mixers', 'Art', 'Food & Drink', 'Tech', 'Comedy'];
const intents = ['chill', 'fun', 'learning', 'networking', 'active', 'creative'];
const adjectives = ['Rooftop', 'Underground', 'Exclusive', 'Casual', 'Weekend', 'Evening', 'Morning', 'Sunset'];

const generateEvents = () => {
  const events = [];
  for (let i = 1; i <= 50; i++) {
    const city = cities[Math.floor(Math.random() * cities.length)];
    const category = categories[Math.floor(Math.random() * categories.length)];
    const intent = intents[Math.floor(Math.random() * intents.length)];
    const adjective = adjectives[Math.floor(Math.random() * adjectives.length)];
    const name = `${adjective} ${category} in ${city}`;
    const price = Math.floor(Math.random() * 3000);
    
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + Math.floor(Math.random() * 30));
    const dateStr = futureDate.toISOString().split('T')[0];

    events.push({
      id: i.toString(),
      name: name,
      category: category,
      mood: intent,
      city: city,
      price: price,
      date: dateStr,
      description: `A ${intent} ${category.toLowerCase()} event happening in ${city}. Join us for a great time!`
    });
  }
  return events;
};

const dataDir = path.join(__dirname, '../data');
if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir);
}

const eventsData = generateEvents();
fs.writeFileSync(path.join(dataDir, 'events.json'), JSON.stringify(eventsData, null, 2));
console.log('Successfully generated 50 events in data/events.json');
