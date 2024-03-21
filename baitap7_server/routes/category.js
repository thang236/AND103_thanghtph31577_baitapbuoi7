import express from "express";

import {
  createCategory,
  fetchAllCategory,
  updateCategory,
  deleteCategory,
} from "../controllers/category.js";
const router = express.Router();

router.post("/create", createCategory);
router.get("/", fetchAllCategory);
router.put("/update/:id", updateCategory);
router.delete("/del/:id", deleteCategory);

export default router;
