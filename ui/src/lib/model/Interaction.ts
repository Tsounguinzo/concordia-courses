export type InteractionKind = 'like' | 'dislike';

export type Interaction = {
  kind: InteractionKind;
  courseId: string;
  instructorId: string;
  userId: string;
  referrer: string;
};
