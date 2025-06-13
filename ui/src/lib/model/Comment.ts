export interface Comment {
  _id: string;
  userId: string;
  content: string;
  timestamp: string; // Aligning with DTO, which uses String for serialized LocalDateTime
}
