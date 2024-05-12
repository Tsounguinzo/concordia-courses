import type {Schedule} from './model/Schedule';
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

export const getCurrentTerms = (): [string, string, string, string, string] => {
    const now = new Date();
    const month = now.getMonth() + 1;
    const year = now.getFullYear();

    if (month >= 8)
        return [`Fall ${year}`, `Fall/Winter ${year}-${year + 1}`, `Winter ${year + 1}`, `Spring ${year + 1}` , `Summer ${year + 1}`];

    return [`Fall ${year - 1}`, `Fall/Winter ${year - 1}-${year}`, `Winter ${year}`, `Spring ${year}` , `Summer ${year}`];
};

export const addAcademicYear = (term: string) => {
    const now = new Date();
    const month = now.getMonth() + 1;
    const year = now.getFullYear();

    if (month >= 8)
        return [`Fall ${year}`, `Fall/Winter ${year}-${year + 1}`, `Winter ${year + 1}`, `Spring ${year + 1}` , `Summer ${year + 1}`].filter(current => current.split(" ")[0] === term)[0];

    return [`Fall ${year - 1}`, `Fall/Winter ${year - 1}-${year}`, `Winter ${year}`, `Spring ${year}` , `Summer ${year}`].filter(current => current.split(" ")[0] === term)[0];
};

export const sortTerms = (terms: string[]) => {
    const order = ['Fall', 'Fall/Winter', 'Winter', 'Spring', 'Summer'];

    return terms.sort((a, b) => {
        return a === b ? 0 : order.indexOf(a) - order.indexOf(b);
    });
};

export const sortSchedulesByBlocks = (schedules: Schedule[]) => {
    const order = ['LEC', 'TUT', 'LAB'];

    return schedules.sort((a, b) => {
        const aNum = a.blocks[0].section;
        const bNum = b.blocks[0].section;
        const aType = a.blocks[0].componentCode;
        const bType = b.blocks[0].componentCode;

        return aType === bType
            ? aNum.localeCompare(bNum)
            : order.indexOf(aType) - order.indexOf(bType);
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
    const seconds = Math.floor((now.valueOf() - date.valueOf()) / 1000);

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