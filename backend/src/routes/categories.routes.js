const router = require('express').Router();
const db = require('../database/db');
router.get('/', (req, res) => {
  const categories = db.prepare('SELECT id, name, description FROM expense_categories ORDER BY name').all();
  res.json(categories);
});
module.exports = router;
