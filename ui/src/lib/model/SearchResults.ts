import type { CourseData } from '../searchIndex';
import type { Instructor } from './Instructor';

export type SearchResults = {
  query?: string;
  courses: CourseData[];
  instructors: Instructor[];
};
