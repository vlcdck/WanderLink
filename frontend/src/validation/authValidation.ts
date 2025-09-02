import Joi from "joi";

export const registerSchema = Joi.object({
    email: Joi.string().email({ tlds: { allow: false } }).required().messages({
        "string.empty": "Email обов'язковий",
        "string.email": "Невірний формат email",
    }),
    password: Joi.string().min(6).required().messages({
        "string.empty": "Пароль обов'язковий",
        "string.min": "Пароль повинен бути не менше 6 символів",
    }),
    username: Joi.string().min(3).required().messages({
        "string.empty": "Username обов'язковий",
        "string.min": "Username повинен бути не менше 3 символів",
    }),
    firstName: Joi.string().required().messages({
        "string.empty": "Ім'я обов'язкове",
    }),
    lastName: Joi.string().required().messages({
        "string.empty": "Прізвище обов'язкове",
    }),
});

export const loginSchema = Joi.object({
    email: Joi.string().email({ tlds: { allow: false } }).required().messages({
        "string.empty": "Email обов'язковий",
        "string.email": "Невірний формат email",
    }),
    password: Joi.string().required().messages({
        "string.empty": "Пароль обов'язковий",
    }),
});
