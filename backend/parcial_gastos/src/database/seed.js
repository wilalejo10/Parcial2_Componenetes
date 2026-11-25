const db = require('./db');
const categories = require('../config/categories');
const insertCategory = db.prepare('INSERT OR IGNORE INTO expense_categories (name, description) VALUES (?, ?)');
for (const category of categories) insertCategory.run(category.name, category.description);
console.log('Categorías iniciales verificadas.');
db.close();
