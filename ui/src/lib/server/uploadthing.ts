import { createUploadthing, type FileRouter } from "uploadthing/server";

const f = createUploadthing();

export const ourFileRouter = {
    gradeDistribution: f({ image: { maxFileSize: "4MB" } })
        .middleware(async () => {
            return {};
        })
        .onUploadComplete(async ({ file }) => {
            console.log("Upload complete for:", file.name);
            return { url: file.url };
        }),
} satisfies FileRouter;

export type OurFileRouter = typeof ourFileRouter;