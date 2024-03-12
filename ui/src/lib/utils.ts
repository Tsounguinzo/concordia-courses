import type {Schedule} from './model/Schedule';

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

export const variantToSize = (variant: 'small' | 'large') => {
  return variant === 'small' ? 20 : 18;
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
            fall: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" ${size} viewBox="0 0 24 24" fill="brown" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-leaf"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/></svg>`,
            winter: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" ${size} viewBox="0 0 24 24" fill="skyblue" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-snowflake"><line x1="2" x2="22" y1="12" y2="12"/><line x1="12" x2="12" y1="2" y2="22"/><path d="m20 16-4-4 4-4"/><path d="m4 8 4 4-4 4"/><path d="m16 4-4 4-4-4"/><path d="m8 20 4-4 4 4"/></svg>`,
            summer: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" ${size} viewBox="0 0 24 24" fill="orange" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-sun"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`,
            fall_winter: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="skyblue" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-wind"><path d="M17.7 7.7a2.5 2.5 0 1 1 1.8 4.3H2"/><path d="M9.6 4.6A2 2 0 1 1 11 8H2"/><path d="M12.6 19.4A2 2 0 1 0 14 16H2"/></svg>`,
            spring: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="yellow" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-flower-2"><path d="M12 5a3 3 0 1 1 3 3m-3-3a3 3 0 1 0-3 3m3-3v1M9 8a3 3 0 1 0 3 3M9 8h1m5 0a3 3 0 1 1-3 3m3-3h-1m-2 3v-1"/><circle cx="12" cy="8" r="2"/><path d="M12 10v12"/><path d="M12 22c4.2 0 7-1.667 7-5-4.2 0-7 1.667-7 5Z"/><path d="M12 22c-4.2 0-7-1.667-7-5 4.2 0 7 1.667 7 5Z"/></svg>`,
        };


        return icons[(term === 'Fall/Winter') ? term.replace('/','_').toLowerCase() : term.toLowerCase()];
    };
