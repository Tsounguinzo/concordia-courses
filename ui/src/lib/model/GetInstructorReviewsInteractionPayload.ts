import type {Interaction} from "$lib/model/Interaction";

export type GetInstructorReviewsInteractionPayload = {
    instructorId: string;
    interactions: Interaction[];
};