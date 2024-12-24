import type {Review} from "$lib/model/Review";
import type {Instructor} from "$lib/model/Instructor";

export type GetInstructorWithReviewsPayload = {
    instructor: Instructor;
    reviews: Review[];
    totalReviews: number;
    hasUserReviewed: boolean;
};