const router = require('express').Router();
const db = require('../database/db');

router.get('/', (req, res) => {
  const trips = db.prepare(`SELECT t.id, t.name, t.destination, t.start_date AS startDate, t.end_date AS endDate,
    COUNT(e.id) AS expenseCount, COALESCE(SUM(e.amount), 0) AS totalExpenses
    FROM trips t LEFT JOIN expenses e ON e.trip_id = t.id
    GROUP BY t.id ORDER BY t.id DESC`).all();
  res.json(trips);
});

router.post('/', (req, res) => {
  const { name, destination, startDate, endDate } = req.body;
  if (![name, destination, startDate, endDate].every(v => typeof v === 'string' && v.trim()))
    return res.status(400).json({ message: 'name, destination, startDate y endDate son obligatorios.' });
  if (!/^\d{4}-\d{2}-\d{2}$/.test(startDate) || !/^\d{4}-\d{2}-\d{2}$/.test(endDate) || endDate < startDate)
    return res.status(400).json({ message: 'Las fechas deben tener formato YYYY-MM-DD y la fecha final no puede ser anterior a la inicial.' });
  const result = db.prepare('INSERT INTO trips (name, destination, start_date, end_date) VALUES (?, ?, ?, ?)')
    .run(name.trim(), destination.trim(), startDate, endDate);
  res.status(201).json(db.prepare('SELECT id, name, destination, start_date AS startDate, end_date AS endDate FROM trips WHERE id = ?').get(Number(result.lastInsertRowid)));
});

router.get('/:id', (req, res) => {
  const trip = db.prepare(`SELECT t.id, t.name, t.destination, t.start_date AS startDate, t.end_date AS endDate,
    COALESCE(SUM(e.amount), 0) AS totalExpenses, COUNT(e.id) AS expenseCount
    FROM trips t LEFT JOIN expenses e ON e.trip_id = t.id WHERE t.id = ? GROUP BY t.id`).get(Number(req.params.id));
  if (!trip) return res.status(404).json({ message: 'Viaje no encontrado.' });
  trip.expenses = db.prepare(`SELECT e.id, e.description, e.amount, e.expense_date AS expenseDate,
    c.id AS categoryId, c.name AS categoryName FROM expenses e
    JOIN expense_categories c ON c.id = e.category_id WHERE e.trip_id = ? ORDER BY e.expense_date, e.id`).all(trip.id);
  res.json(trip);
});
module.exports = router;
