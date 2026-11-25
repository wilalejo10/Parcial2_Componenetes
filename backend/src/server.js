const express = require('express');
const swaggerUi = require('swagger-ui-express');
const db = require('./database/db');
const tripsRoutes = require('./routes/trips.routes');
const expensesRoutes = require('./routes/expenses.routes');
const categoriesRoutes = require('./routes/categories.routes');
const { notFound, errorHandler } = require('./middleware/errors');
const app = express();
app.use(express.json());
app.get('/api/health', (req, res) => res.json({ status: 'ok' }));
app.use('/api/trips', tripsRoutes);
app.use('/api/expenses', expensesRoutes);
app.use('/api/categories', categoriesRoutes);
const swagger = { openapi: '3.0.3', info: { title: 'API - Viajes y gastos', version: '1.0.0' }, servers: [{ url: 'http://localhost:3000' }], paths: {
  '/api/trips': { get: { summary: 'Consultar viajes' }, post: { summary: 'Crear viaje' } },
  '/api/trips/{id}': { get: { summary: 'Consultar detalle de viaje y gastos' } },
  '/api/expenses': { get: { summary: 'Consultar gastos; permite filtrar por tripId' }, post: { summary: 'Registrar gasto' } },
  '/api/categories': { get: { summary: 'Consultar categorías de gastos' } }
}};
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swagger));
app.use(notFound);
app.use(errorHandler);
const port = Number(process.env.PORT || 3000);
const server = app.listen(port, () => console.log(`API disponible en http://localhost:${port}`));
function shutdown() { server.close(() => { db.close(); process.exit(0); }); }
process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);
