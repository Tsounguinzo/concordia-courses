export type Review = {
  content: string;
  courseId: string;
  instructor: string;
  rating: number; // 0-5
  difficulty: number; // 0-5
  timestamp: Date;
  userId: string;
  likes: number;
};
