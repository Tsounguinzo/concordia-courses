import type {PageLoad} from './$types';
import type {HomePageStats} from '$lib/model/HomePageStats';

const FALLBACK_STATS: HomePageStats = {
    totalCourses: 7832,
    totalReviews: 64506,
    totalInstructors: 4872
};

export const load: PageLoad = async ({fetch}) => {
    try {
        const response = await fetch('/api/v1/courses/home/stats');
        if (!response.ok) {
            return {homeStats: FALLBACK_STATS};
        }

        const homeStats = await response.json() as HomePageStats;
        return {homeStats};
    } catch (error) {
        return {homeStats: FALLBACK_STATS};
    }
};
