import type { CourseData } from '../searchIndex';

export type SearchResults = {
  query?: string;
  courses: CourseData[];
};
