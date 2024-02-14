import type {Course} from './model/Course';
import type {Instructor} from './model/Instructor';
import type {Schedule} from './model/Schedule';

export const uniqueTermInstructors = (course: Course) => {
    const termInstructors = course.instructors.filter((i) =>
        course.terms.includes(i.term)
    );

    const unique = [];
    const filledTerms = new Set();

    for (const instructor of termInstructors) {
        if (!filledTerms.has(instructor.term)) {
            unique.push(instructor);
            filledTerms.add(instructor.term);
        }
    }

    const order = ['Fall', 'Fall/Winter', 'Winter', 'Spring', 'Summer'];

    for (const term of course.terms) {
        if (!filledTerms.has(term))
            unique.push({term, name: term});
    }

    unique.sort((a, b) => order.indexOf(a.term) - order.indexOf(b.term));

    return unique;
};

export const getCurrentTerms = (): [string, string, string, string, string] => {
    const now = new Date();
    const month = now.getMonth() + 1;
    const year = now.getFullYear();

    if (month >= 8)
        return [`Fall ${year}`, `Fall/Winter ${year}-${year + 1}`, `Winter ${year + 1}`, `Spring ${year + 1}` , `Summer ${year + 1}`];

    return [`Fall ${year - 1}`, `Fall/Winter ${year - 1}-${year}`, `Winter ${year}`, `Spring ${year}` , `Summer ${year}`];
};

export const filterCurrentInstructors = (instructors: Instructor[]) => {
    const currentTerm = getCurrentTerms();
    return instructors.filter((i) => currentTerm.includes(i.term));
};

export const sortTerms = (terms: string[]) => {
    const order = ['Fall', 'Fall/Winter', 'Winter', 'Spring', 'Summer'];

    return terms.sort((a, b) => {
        return a.split(' ')[1] === b.split(' ')[1]
            ? order.indexOf(a.split(' ')[0]) - order.indexOf(b.split(' ')[0])
            : parseInt(a.split(' ')[1], 10) - parseInt(b.split(' ')[1], 10);
    });
};

export const sortSchedulesByBlocks = (schedules: Schedule[]) => {
    const order = ['LEC', 'TUT', 'LAB'];

    return schedules.sort((a, b) => {
        const aNum = a.blocks[0].classNumber;
        const bNum = b.blocks[0].classNumber;
        const aType = a.blocks[0].componentCode;
        const bType = b.blocks[0].componentCode;

        return aType === bType
            ? aNum - bNum
            : order.indexOf(aType) - order.indexOf(bType);
    });
};

export const getUrl = (): string => import.meta.env.VITE_API_URL ?? '';

export const courseIdToUrlParam = (courseId: string) =>
    `${courseId.slice(0, 4)}-${courseId.slice(4)}`.toLowerCase();

export const capitalize = (s: string): string =>
    s.charAt(0).toUpperCase() + s.slice(1);

export const punctuate = (s: string): string =>
    s.charAt(s.length - 1) === '.' ? s : s + '.';

export const isValidCourseCode = (s: string) =>
    /^(([A-Z0-9]){4} [0-9]{3,4})$/.test(s);

export const spliceCourseCode = (courseCode: string, delimiter: string) =>
    courseCode.slice(0, 4) + delimiter + courseCode.slice(4);

export const round2Decimals = (n: number) => Math.round(n * 100) / 100;

export const mod = (n: number, m: number) => ((n % m) + m) % m;

export function multiplySvgPoints(pointsString: string, iconSize: number, width: number, height: number) {
  const iconMaxSize = Math.min(width, height) * 0.5;
  const scale = iconMaxSize / iconSize;
  // eslint-disable-next-line
  return pointsString.replace(/([0-9,\.]{1,})/g, (coords) => {
    // eslint-disable-next-line
    coords = coords.split(',').map((p) => parseFloat(p));
    const x = coords[0] * scale + width / 2 - (iconSize * scale) / 2;
    const y = coords[1] * scale + height / 2 - (iconSize * scale) / 2;
    if (iconMaxSize >= 100) {
      return `${Math.round(x)},${Math.round(y)}`;
    }
    return `${x},${y}`;
  });
}
