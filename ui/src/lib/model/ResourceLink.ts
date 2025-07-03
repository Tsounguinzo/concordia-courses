export interface ResourceLink {
  _id: string;
  userId: string;
  url: string;
  description: string;
  timestamp: string; // Aligning with DTO, which uses String for serialized LocalDateTime
}
