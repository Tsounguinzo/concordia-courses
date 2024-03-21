export type Review = {
  _id: string;
  content: string;
  courseId: string;
  instructor: string;
  experience: number; // 0-5
  difficulty: number; // 0-5
  timestamp: Date;
  userId: string;
  likes: number;
};
