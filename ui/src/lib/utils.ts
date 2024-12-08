import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';
import type {Review} from "$lib/model/Review";
import { format } from 'date-fns';
import {toZonedTime } from 'date-fns-tz';
import capitalize from "lodash/capitalize.js";

export const ISOFormattedDateUTC4 = (date: string | number | Date) => {
    const timeZone = 'America/New_York';
    const zonedDate = toZonedTime(date, timeZone);
    return format(zonedDate, "yyyy-MM-dd'T'HH:mm:ss.SSS").replace('Z', '');
};

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export const sortTerms = (terms: string[]) => {
    const order = ['Fall', 'Fall/Winter', 'Winter', 'Spring', 'Summer'];

    return terms.sort((a, b) => {
        const [aTerm, aYear] = a.split(' ');
        const [bTerm, bYear] = b.split(' ');

        // Special case: ex "Summer 2024" comes before "Fall 2024"
        if (aTerm === 'Summer' && bTerm === 'Fall' && aYear === bYear) {
            return -1;
        }

        if (aTerm === 'Fall' && bTerm === 'Summer' && aYear === bYear) {
            return 1;
        }

        // Compare the years first
        const yearComparison = parseInt(aYear, 10) - parseInt(bYear, 10);

        if (yearComparison !== 0) {
            return yearComparison;
        }

        // If the years are the same, compare the term types using the predefined order
        return order.indexOf(aTerm) - order.indexOf(bTerm);
    });
};

export const courseIdToUrlParam = (courseId: string) =>
    `${courseId?.slice(0, 4)}-${courseId?.slice(4)}`.toLowerCase();

export const instructorIdToUrlParam = (firstName: string, lastName: string) =>
    `${firstName}-${lastName}`.replaceAll(/\s+/g, '-').toLowerCase();

export const instructorNameToUrlParam = (name: string) =>
    name.replaceAll(/\s+/g, '-').toLowerCase();

export const instructorIdToName = (id: string) =>
    id?.split('-').map(capitalize).join(' ');

export const courseNameToId = (name: string) =>
    name.replaceAll(/[^a-zA-Z0-9]/g, '').toUpperCase();

export const schoolIdToName = (id: string) =>
    id?.split('-').map(capitalize).join(' ');

export const spliceCourseCode = (courseCode: string, delimiter: string) => {
    const firstIntIndex = courseCode?.search(/\d/);
    if (firstIntIndex === undefined || firstIntIndex === -1) {
        return courseCode;
    }
    return courseCode?.slice(0, firstIntIndex) + delimiter + courseCode?.slice(firstIntIndex);
}

export const round2Decimals = (n: number) => Math.round(n * 100) / 100;

export function multiplySvgPoints(pointsString: string, iconSize: number, width: number, height: number) {
  const iconMaxSize = Math.min(width, height) * 0.5;
  const scale = iconMaxSize / iconSize;
  // eslint-disable-next-line
  return pointsString.replace(/([0-9,.]+)/g, (coords: string | number[]) => {
    // eslint-disable-next-line
      if (typeof coords === "string") {
          coords = coords.split(',').map((p) => parseFloat(p));
      }
    const x = coords[0] * scale + width / 2 - (iconSize * scale) / 2;
    const y = coords[1] * scale + height / 2 - (iconSize * scale) / 2;
    if (iconMaxSize >= 100) {
      return `${Math.round(x)},${Math.round(y)}`;
    }
    return `${x},${y}`;
  });
}

export const variantToSize = (variant: 'small' | 'large') => {
  return variant === 'small' ? 24 : 20;
};

export const levelsOptions = ['1XX', '2XX', '3XX', '4XX', '5XX', '6XX', '7XX', '8XX', '9XX'];
export const moreLevelsOptions = ['1XXX', '2XXX', '4XXX', '5XXX', '6XXX', '7XXX', '8XXX', '9XXX'];
export const termColorMap: Record<string, string> = {
        fall: 'bg-red-100 text-red-900',
        winter: 'bg-sky-100 text-sky-900',
        summer: 'bg-yellow-100 text-yellow-900',
        fall_winter: 'bg-[#fcddc9] text-[#d5590c]',
        spring: 'bg-orange-100 text-orange-900',
    };

export const termToIcon = (term: string, variant: 'small' | 'large') => {
        type IconMap = { [key: string]: string };
        const size = variantToSize(variant);
        term = term.split(" ")[0];

        const icons: IconMap = {
            fall: `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}"  viewBox="0 0 24 24" fill="brown" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-leaf"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/></svg>`,
            winter: `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="skyblue" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-snowflake"><line x1="2" x2="22" y1="12" y2="12"/><line x1="12" x2="12" y1="2" y2="22"/><path d="m20 16-4-4 4-4"/><path d="m4 8 4 4-4 4"/><path d="m16 4-4 4-4-4"/><path d="m8 20 4-4 4 4"/></svg>`,
            summer: `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="orange" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-sun"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`,
            fall_winter: `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="skyblue" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-wind"><path d="M17.7 7.7a2.5 2.5 0 1 1 1.8 4.3H2"/><path d="M9.6 4.6A2 2 0 1 1 11 8H2"/><path d="M12.6 19.4A2 2 0 1 0 14 16H2"/></svg>`,
            spring: `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="yellow" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-flower-2"><path d="M12 5a3 3 0 1 1 3 3m-3-3a3 3 0 1 0-3 3m3-3v1M9 8a3 3 0 1 0 3 3M9 8h1m5 0a3 3 0 1 1-3 3m3-3h-1m-2 3v-1"/><circle cx="12" cy="8" r="2"/><path d="M12 10v12"/><path d="M12 22c4.2 0 7-1.667 7-5-4.2 0-7 1.667-7 5Z"/><path d="M12 22c-4.2 0-7-1.667-7-5 4.2 0 7 1.667 7 5Z"/></svg>`,
        };


        return icons[(term === 'Fall/Winter') ? term.replace('/','_').toLowerCase() : term.toLowerCase()];
    };

export const experienceToIcon = (experience: number): [string, string] => {
    if (experience <= 0) {
        return ['text-gray-500', '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-hourglass"><path d="M5 22h14"/><path d="M5 2h14"/><path d="M17 22v-4.172a2 2 0 0 0-.586-1.414L12 12l-4.414 4.414A2 2 0 0 0 7 17.828V22"/><path d="M7 2v4.172a2 2 0 0 0 .586 1.414L12 12l4.414-4.414A2 2 0 0 0 17 6.172V2"/></svg>']
    } else if (experience > 0 && experience <= 1) {
        return ['text-red-700', '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-angry"><circle cx="12" cy="12" r="10"/><path d="M16 16s-1.5-2-4-2-4 2-4 2"/><path d="M7.5 8 10 9"/><path d="m14 9 2.5-1"/><path d="M9 10h0"/><path d="M15 10h0"/></svg>']
    } else if (experience > 1 && experience <= 2) {
        return ['text-orange-500', '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-frown"><circle cx="12" cy="12" r="10"/><path d="M16 16s-1.5-2-4-2-4 2-4 2"/><line x1="9" x2="9.01" y1="9" y2="9"/><line x1="15" x2="15.01" y1="9" y2="9"/></svg>']
    } else if (experience > 2 && experience <= 3) {
        return ['text-teal-400', '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-smile"><circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2"/><line x1="9" x2="9.01" y1="9" y2="9"/><line x1="15" x2="15.01" y1="9" y2="9"/></svg>']
    } else if (experience > 3 && experience <= 4) {
        return ['text-green-600', '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-laugh"><circle cx="12" cy="12" r="10"/><path d="M18 13a6 6 0 0 1-6 5 6 6 0 0 1-6-5h12Z"/><line x1="9" x2="9.01" y1="9" y2="9"/><line x1="15" x2="15.01" y1="9" y2="9"/></svg>']
    } else if (experience > 4 && experience <= 5) {
        return ['text-yellow-700', '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-crown"><path d="M11.562 3.266a.5.5 0 0 1 .876 0L15.39 8.87a1 1 0 0 0 1.516.294L21.183 5.5a.5.5 0 0 1 .798.519l-2.834 10.246a1 1 0 0 1-.956.734H5.81a1 1 0 0 1-.957-.734L2.02 6.02a.5.5 0 0 1 .798-.519l4.276 3.664a1 1 0 0 0 1.516-.294z"/><path d="M5 21h14"/></svg>']
    } else {
        return ['text-blue-500', '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-loader"><line x1="12" x2="12" y1="2" y2="6"/><line x1="12" x2="12" y1="18" y2="22"/><line x1="4.93" x2="7.76" y1="4.93" y2="7.76"/><line x1="16.24" x2="19.07" y1="16.24" y2="19.07"/><line x1="2" x2="6" y1="12" y2="12"/><line x1="18" x2="22" y1="12" y2="12"/><line x1="4.93" x2="7.76" y1="19.07" y2="16.24"/><line x1="16.24" x2="19.07" y1="7.76" y2="4.93"/></svg>']
    }
}

export function determineReviewFor(review: Review) {
    switch (review.type) {
        case 'course':
            return spliceCourseCode(review.courseId, ' ');
        case 'instructor':
            return instructorIdToName(review.instructorId);
        case 'school':
            return schoolIdToName(review.schoolId);
        default:
            return 'Unknown';
    }
}

export const timeFrame = (date: Date, now: Date) => {
    const timeZone = 'America/New_York';
    const seconds = Math.floor((toZonedTime(now, timeZone).valueOf() - toZonedTime(date, timeZone).valueOf()) / 1000);

    let interval = Math.floor(seconds / 31536000);
    if (interval >= 1) return `${interval} year${interval > 1 ? 's' : ''} ago`;

    interval = Math.floor(seconds / 2592000);
    if (interval >= 1) return `${interval} month${interval > 1 ? 's' : ''} ago`;

    interval = Math.floor(seconds / 86400);
    if (interval >= 1) return `${interval} day${interval > 1 ? 's' : ''} ago`;

    interval = Math.floor(seconds / 3600);
    if (interval >= 1) return `${interval} hour${interval > 1 ? 's' : ''} ago`;

    interval = Math.floor(seconds / 60);
    if (interval >= 1) return `${interval} minute${interval > 1 ? 's' : ''} ago`;

    return seconds === 0 ? 'now' : `${Math.floor(seconds)} second${seconds > 1 ? 's' : ''} ago`;
};

export function convertGradePointsToLetter(gradePoints: number): string {
    if (gradePoints >= 4.3) {
        return 'A+';
    } else if (gradePoints >= 4.0) {
        return 'A';
    } else if (gradePoints >= 3.7) {
        return 'A-';
    } else if (gradePoints >= 3.3) {
        return 'B+';
    } else if (gradePoints >= 3.0) {
        return 'B';
    } else if (gradePoints >= 2.7) {
        return 'B-';
    } else if (gradePoints >= 2.3) {
        return 'C+';
    } else if (gradePoints >= 2.0) {
        return 'C';
    } else if (gradePoints >= 1.7) {
        return 'C-';
    } else if (gradePoints >= 1.3) {
        return 'D+';
    } else if (gradePoints >= 1.0) {
        return 'D';
    } else if (gradePoints >= 0.7) {
        return 'D-';
    } else {
        return 'F';
    }
}

type Course = {
    _id: string;
    title: string;
    subject: string;
    catalog: string;
    instructors: string[];
};

type UrlEntry = {
    loc: string;
    changefreq: string;
    priority: number;
};


export const generateSitemapEntries = (courses: Course[]): UrlEntry[] => {
    const urls: UrlEntry[] = [];

    courses.forEach((course) => {
        const courseUrl = `https://concordia.courses/course/${courseIdToUrlParam(course._id)}`;
        urls.push({
            loc: courseUrl,
            changefreq: 'daily',
            priority: 1.0,
        });

        course.instructors.forEach((instructor) => {
            const instructorUrl = `https://concordia.courses/instructor/${instructorNameToUrlParam(instructor)}`;
            urls.push({
                loc: instructorUrl,
                changefreq: 'daily',
                priority: 1.0,
            });
        });
    });

    return urls;
};

export function getCampusLocation(locationCode: String) {
    switch (locationCode) {
        case 'SGW':
            return {
                "@type": "Place",
                "name": "Concordia University - Sir George Williams Campus",
                "address": {
                    "@type": "PostalAddress",
                    "streetAddress": "1455 De Maisonneuve Blvd W",
                    "addressLocality": "Montreal",
                    "addressRegion": "QC",
                    "postalCode": "H3G 1M8",
                    "addressCountry": "Canada"
                }
            };
        case 'LOY':
            return {
                "@type": "Place",
                "name": "Concordia University - Loyola Campus",
                "address": {
                    "@type": "PostalAddress",
                    "streetAddress": "7141 Sherbrooke St W",
                    "addressLocality": "Montreal",
                    "addressRegion": "QC",
                    "postalCode": "H4B 1R6",
                    "addressCountry": "Canada"
                }
            };
        case 'PI':
            return {
                "@type": "Place",
                "name": "Power Institute",
                "address": {
                    "@type": "PostalAddress",
                    "streetAddress": "1455 De Maisonneuve Blvd W",
                    "addressLocality": "Montreal",
                    "addressRegion": "QC",
                    "postalCode": "H3G 1M8",
                    "addressCountry": "Canada"
                }
            };
        // For online courses, we return null and won't include location
        default:
            return null;
    }
}

export function getCourseMode(code: String) {
    if (code === 'ONL') return 'Online';
    if (code === 'BLD') return 'Blended';
    return 'Onsite';
}
