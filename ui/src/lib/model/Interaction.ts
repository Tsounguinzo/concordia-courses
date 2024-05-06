import type {ReviewType} from "$lib/model/Review";

export type InteractionKind = 'like' | 'dislike';

export type Interaction = {
  kind: InteractionKind;
  type: ReviewType;
  courseId: string;
  instructorId: string;
  userId: string;
  referrer: string;
};
