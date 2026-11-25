const router = require('express').Router();
const db = require('../database/db');

router.get('/', (req, res) => {
  const { tripId } = req.query;
  const sql = `SELECT e.id, e.trip_id AS tripId, e.description, e.amount, e.expense_date AS expenseDate,
    c.id AS categoryId, c.name AS categoryName FROM expenses e
    JOIN expense_categories c ON c.id = e.category_id ${tripId ? 'WHERE e.trip_id = ?' : ''}
    ORDER BY e.expense_date DESC, e.id DESC`;
  res.json(tripId ? db.prepare(sql).all(Number(tripId)) : db.prepare(sql).all());
});

router.post('/', (req, res) => {
  const { tripId, description, amount, categoryId, expenseDate } = req.body;
  if (!tripId || !description?.trim() || amount === undefined || !categoryId || !expenseDate)
    return res.status(400).json({ message: 'tripId, description, amount, categoryId y expenseDate son obligatorios.' });
  const numericAmount = Number(amount);
  if (!Number.isFinite(numericAmount) || numericAmount <= 0)
    return res.status(400).json({ message: 'El valor del gasto debe ser un número mayor que cero.' });
  if (!/^\d{4}-\d{2}-\d{2}$/.test(expenseDate))
    return res.status(400).json({ message: 'expenseDate debe tener formato YYYY-MM-DD.' });
  const trip = db.prepare('SELECT id, start_date, end_date FROM trips WHERE id = ?').get(Number(tripId));
  if (!trip) return res.status(404).json({ message: 'Viaje no encontrado.' });
  if (expenseDate < trip.start_date || expenseDate > trip.end_date)
    return res.status(400).json({ message: 'La fecha del gasto debe estar dentro del período del viaje.' });
  const category = db.prepare('SELECT id FROM expense_categories WHERE id = ?').get(Number(categoryId));
  if (!category) return res.status(400).json({ message: 'La categoría indicada no existe.' });
  const result = db.prepare('INSERT INTO expenses (trip_id, category_id, description, amount, expense_date) VALUES (?, ?, ?, ?, ?)')
    .run(Number(tripId), Number(categoryId), description.trim(), numericAmount, expenseDate);
  const expense = db.prepare(`SELECT e.id, e.trip_id AS tripId, e.description, e.amount, e.expense_date AS expenseDate,
    c.id AS categoryId, c.name AS categoryName FROM expenses e JOIN expense_categories c ON c.id = e.category_id WHERE e.id = ?`)
    .get(Number(result.lastInsertRowid));
  res.status(201).json(expense);
});
module.exports = router;
