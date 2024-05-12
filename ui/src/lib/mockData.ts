import {faker} from "@faker-js/faker";
import type {Review} from "$lib/model/Review";

const coursesmock = [
    {"subject": "POLI", "catalog": "397"},
    {"subject": "SPEC", "catalog": "609"},
    {"subject": "RELI", "catalog": "685"},
    {"subject": "FTRA", "catalog": "615"},
    {"subject": "FPST", "catalog": "301"},
    {"subject": "PSYC", "catalog": "321"},
    {"subject": "IRST", "catalog": "209"},
    {"subject": "WILS", "catalog": "601"},
    {"subject": "BLDG", "catalog": "6701"},
    {"subject": "HIST", "catalog": "101"},
    {"subject": "MATH", "catalog": "205"},
    {"subject": "PHYS", "catalog": "284"},
    {"subject": "ECON", "catalog": "201"},
    {"subject": "BIO", "catalog": "205"},
    {"subject": "CHEM", "catalog": "206"},
    {"subject": "MANA", "catalog": "300"},
];

const tags = [
    "Tough Grader",
    "Get Ready To Read",
    "Participation Matters",
    "Extra Credit",
    "Group Projects",
    "Amazing Lectures",
    "Clear Grading Criteria",
    "Gives Good Feedback",
    "Inspirational",
    "Lots Of Homework",
    "Hilarious",
    "Beware Of Pop Quizzes",
    "So Many Papers",
    "Caring",
    "Respected",
    "Flexible Deadlines",
    "Lecture Heavy",
    "Test Heavy",
    "Graded By Few Things",
    "Accessible Outside Class",
    "Online Savvy",
    "Engaging",
    "Technically Proficient",
    "Industry Experienced",
    "Research-Oriented",
    "Multidisciplinary Approach",
    "Interactive Sessions",
    "Encourages Critical Thinking",
    "Uses Multimedia",
    "Culturally Inclusive"
];

function getRandomSubarray(arr, size) {
    const shuffled = arr.slice(0);
    let i = arr.length, temp, index;
    while (i--) {
        index = Math.floor((i + 1) * Math.random());
        temp = shuffled[index];
        shuffled[index] = shuffled[i];
        shuffled[i] = temp;
    }
    return shuffled.slice(0, size);
}

export function createFakeInstructor() {
    return {
        _id: `${faker.person.firstName()}-${faker.person.lastName()}`.toLowerCase(),
        firstName: faker.person.firstName(),
        lastName: faker.person.lastName(),
        department: faker.commerce.department(),
        courses: getRandomSubarray(coursesmock, Math.floor(Math.random() * 10)), // 0 to 10 courses
        tags: getRandomSubarray(tags, Math.floor(Math.random() * 10)), // 0 to 10 tags
        avgDifficulty: parseFloat((Math.random() * 5).toFixed(1)),
        avgRating: parseFloat((Math.random() * 5).toFixed(1)),
        reviewCount: Math.floor(Math.random() * 100)
    };
}

export function createFakeReview(): Review {
    return {
        _id: faker.string.uuid(),
        type: 'instructor',
        content: faker.lorem.sentence(100),
        timestamp: faker.date.recent(),
        likes: Math.floor(Math.random() * 40),
        difficulty: parseFloat((Math.random() * 5).toFixed(1)),
        userId: faker.string.uuid(),
        courseId: getRandomSubarray(coursesmock, 1)[0].subject + getRandomSubarray(coursesmock, 1)[0].catalog,
        instructorId: `${faker.person.firstName()}-${faker.person.lastName()}`.toLowerCase(),
        rating:  parseFloat((Math.random() * 5).toFixed(1)),
        tags: getRandomSubarray(tags, Math.floor(Math.random() * 5)),
        experience: parseFloat((Math.random() * 5).toFixed(1)),
        schoolId: "",
    };
}