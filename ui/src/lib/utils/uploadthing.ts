import type { OurFileRouter } from "$lib/server/uploadthing";

import { generateSvelteHelpers } from "@uploadthing/svelte";

export const { uploadFiles } = generateSvelteHelpers<OurFileRouter>();
