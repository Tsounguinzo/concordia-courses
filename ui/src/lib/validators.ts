export const validateFieldPresence = (fieldValue: string, fieldName: string) => {
    if (fieldValue === '' || fieldValue == null) {
        return `${fieldName} is required`;
    }
    return '';
};

export function validateYear(year: string) {
    const currentYear = new Date().getFullYear();
    if (!year) {
        return "Year is required.";
    } else if (!/^\d{4}$/.test(year)) {
        return "Year must be a four-digit number.";
    } else if (Number(year) < 1974 || Number(year) > currentYear) {
        return `Year must be between 1974 and ${currentYear}.`;
    }
    return "";
}

export function validateFromOptions(fieldValue: string, fieldName: string, options: string[]) {
    if (!fieldValue) {
        return `${fieldName} is required.`;
    }
    const isValid = options.some(option => option.toLowerCase() === fieldValue.toLowerCase());
    if (!isValid) {
        return `${fieldValue} is not a valid option.`;
    }
    return "";
}

export type FileValidationOptions = {
    allowedTypes?: string[];
    maxSizeMB?: number;
};

export function validateFile(file: File | null, fieldName: string, options: FileValidationOptions = {}): string {
    if (!file) {
        return `${fieldName} is required.`;
    }

    const { allowedTypes, maxSizeMB } = options;

    if (allowedTypes && !allowedTypes.includes(file.type)) {
        const acceptedFormats = allowedTypes.map(type => type.split('/').pop()).join(", ");
        return `Accepted formats: ${acceptedFormats}`;
    }

    if (maxSizeMB !== undefined && file.size > maxSizeMB * 1024 * 1024) {
        return `${fieldName} must be smaller than ${maxSizeMB} MB.`;
    }

    return '';
}

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

    const maxRepetitions = 5;
    const words = content.match(/\b\w+\b/g) || [];
    const wordCount = words.length;

    if (wordCount < 10) {
        return 'Review must contain at least 10 words';
    }

    for (let i = 0; i <= words.length - maxRepetitions; i++) {
        if (words.slice(i, i + maxRepetitions).every(word => word === words[i])) {
            return 'Spamming content detected.';
        }
    }

    return null;
}

export const validateTags = (tags: string[]) => {
    if (tags.length > 3) {
        return "No more than 3 tags are allowed";
    }
    return '';
};