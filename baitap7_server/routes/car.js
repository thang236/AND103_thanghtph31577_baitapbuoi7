import express from "express";

import { create, fetchAll, update, deleteCar, getCarById } from "../controllers/car.js";
const router = express.Router();

router.post("/create", create);
router.get("/", fetchAll);
router.get("/:id", getCarById); 
router.put("/update/:id", update);
router.delete("/del/:id", deleteCar);

export default router;
