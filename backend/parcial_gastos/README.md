# Backend - Segundo parcial: gestión de gastos de viaje

API REST desarrollada con Node.js, Express y SQLite para registrar viajes y administrar los gastos asociados a cada viaje.

## Requisitos
- Node.js 22.13.0 o superior.
- npm.

No es necesario instalar ni configurar un servidor de base de datos. SQLite se utiliza mediante el módulo `node:sqlite` incluido en Node.js.

## Instalación y ejecución

```bash
npm install
npm run seed
npm start
```

La API estará disponible en `http://localhost:3000` y la documentación en `http://localhost:3000/api-docs`.

La base de datos se crea automáticamente en `data/viajes_gastos.db`.

## Endpoints

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/trips` | Consultar viajes con cantidad y total de gastos |
| POST | `/api/trips` | Registrar un viaje |
| GET | `/api/trips/:id` | Consultar detalle de viaje y gastos registrados |
| GET | `/api/expenses?tripId=1` | Consultar gastos de un viaje |
| POST | `/api/expenses` | Registrar un gasto |
| GET | `/api/categories` | Consultar categorías de gastos |
| GET | `/api/health` | Verificar estado de la API |

## Ejemplos de peticiones

### Crear viaje - POST /api/trips

```json
{
  "name": "Europa 2026",
  "destination": "Italia",
  "startDate": "2026-06-10",
  "endDate": "2026-06-20"
}
```

### Registrar gasto - POST /api/expenses

```json
{
  "tripId": 1,
  "description": "Cena en Trastevere",
  "amount": 28.50,
  "categoryId": 1,
  "expenseDate": "2026-06-12"
}
```

### Categorías iniciales

Las categorías se cargan con `npm run seed`. Se pueden consultar mediante `GET /api/categories`. El identificador `categoryId` que se envía al registrar un gasto corresponde al campo `id` devuelto por ese endpoint.

## Validaciones
- Los campos obligatorios deben estar presentes.
- La fecha final del viaje no puede ser anterior a la fecha inicial.
- El valor del gasto debe ser mayor que cero.
- La fecha del gasto debe estar dentro del período del viaje.
- El viaje y la categoría deben existir.
