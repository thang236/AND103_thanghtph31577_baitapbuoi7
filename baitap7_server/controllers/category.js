import Category from "../model/category.js";

//create new category controller
export const createCategory = async (req, res) => {
  try {
    const category = new Category(req.body);
    const { name } = category;
    const categoryExist = await Category.findOne({ name });
    if (categoryExist) {
      return res.status(400).json({ message: "Category already exist" });
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
export const fetchAllCategory = async (req, res) => {
  try {
    const categories = await Category.find();
    if (categories.length === 0) {
      return res.status(404).json({ message: "No category found" });
    }
    res.status(200).json({ status: "200", data: categories });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

//update category controller
export const updateCategory = async (req, res) => {
  try {
    const id = req.params.id;
    const category = await Category.findById(id);
    if (!category) {
      return res.status(404).json({ message: "Category not found" });
    }
    const updateData = await Category.findByIdAndUpdate(id, req.body, {
      new: true,
    });
    res.status(200).json({ status: "200", data: updateData });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

//delete category controller
export const deleteCategory = async (req, res) => {
  try {
    const id = req.params.id;
    const category = await Category.findById(id);
    if (!category) {
      return res.status(404).json({ message: "Category not found" });
    }
    await Category.findByIdAndDelete(id);
    res.status(200).json({ status: "200", message: "Deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};
