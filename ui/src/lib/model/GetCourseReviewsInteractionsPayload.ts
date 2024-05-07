import type { Interaction } from './Interaction';

export type GetCourseReviewsInteractionPayload = {
  courseId: string;
  interactions: Interaction[];
};
