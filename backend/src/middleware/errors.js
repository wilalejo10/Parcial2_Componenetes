function notFound(req, res) { res.status(404).json({ message: 'Endpoint no encontrado.' }); }
function errorHandler(err, req, res, next) {
  console.error(err);
  if (err.code === 'SQLITE_CONSTRAINT_FOREIGNKEY') return res.status(400).json({ message: 'El viaje o la categoría indicada no existe.' });
  if (err.code === 'SQLITE_CONSTRAINT_UNIQUE') return res.status(409).json({ message: 'El registro ya existe.' });
  res.status(500).json({ message: 'Error interno del servidor.' });
}
module.exports = { notFound, errorHandler };
