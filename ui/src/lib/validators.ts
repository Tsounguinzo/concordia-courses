export const validateFieldPresence = (fieldValue: string, fieldName: string) => {
    if (fieldValue === '' || fieldValue == null) {
        return `${fieldName} is required`;
    }
    return '';
};

export const validateNumericRange = (fieldValue: number, fieldName: string, min: number, max: number) => {
    if (fieldValue === 0) {
        return `${fieldName} is required`;
    } else if (fieldValue < min || fieldValue > max) {
        return `${fieldName} must be between ${min} and ${max}`;
    }
    return '';
};

export function validateReviewContent(content: string) {
    if (!content) {
        return 'Review content is required';
    }

    const wordCount = (content.match(/\b\w+\b/g) || []).length;

    if (wordCount < 10) {
        return 'Review must contain at least 10 words';
    }

    return null;
}

export const validateTags = (tags: string[]) => {
    if (tags.length > 3) {
        return "No more than 3 tags are allowed";
    }
    return '';
};