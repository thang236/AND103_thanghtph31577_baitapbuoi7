import express from "express";
import mongoose from "mongoose";
import bodyParser from "body-parser";
import dotenv from "dotenv";
import categoryRoutes from "./routes/category.js";
import carRoutes from "./routes/car.js";
const app = express();
app.use(bodyParser.json());

dotenv.config();

const PORT = 3000 || 1432;
const MONGOURL = "mongodb://localhost:27017/baitap7";

mongoose
  .connect(MONGOURL)
  .then(() => {
    console.log("Database connected successfully");
    app.listen(PORT, () => {
      console.log("====================================");
      console.log(`Server is running at ${PORT}`);
      console.log(
        `Click the link to open dashboard http://localhost:${PORT}/}`
      );
      console.log("====================================");
    });
  })
  .catch((error) => console.log(error));

// Import and use category routes
app.use("/api/category", categoryRoutes);
app.use("/api/car", carRoutes);
