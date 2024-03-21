import Car from "../model/car.js";

//create new category controller
export const create = async (req, res) => {
  try {
    const category = new Car(req.body);
    const { name } = category;
    const categoryExist = await Car.findOne({ name });
    if (categoryExist) {
      return res.status(400).json({ message: "Car already exist" });
    }
    const saveData = await category.save();
    res.status(201).json({
      status: "200",
      message: "Created successfully",
      data: saveData,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

//fetch all category controller
export const fetchAll = async (req, res) => {
  try {
    const categories = await Car.find();
    if (categories.length === 0) {
      return res.status(404).json({ message: "No car found" });
    }
    res.status(200).json({ status: "200", data: categories });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

//update category controller
export const update = async (req, res) => {
  try {
    const id = req.params.id;
    const category = await Car.findById(id);
    if (!category) {
      return res.status(404).json({ message: "Car not found" });
    }
    const updateData = await Car.findByIdAndUpdate(id, req.body, {
      new: true,
    });
    res.status(200).json({ status: "200", data: updateData });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

//delete category controller
export const deleteCar = async (req, res) => {
  try {
    const id = req.params.id;
    const category = await Car.findById(id);
    if (!category) {
      return res.status(404).json({ message: "Car not found" });
    }
    await Car.findByIdAndDelete(id);
    res.status(200).json({ status: "200", message: "Deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

//get car by id 
export const getCarById = async (req, res) => {
  try {
    const id = req.params.id;
    const category = await Car.findById(id);
    if (!category) {
      return res.status(404).json({ message: "Car not found" });
    }
    res.status(200).json({ status: "200", data: category });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};