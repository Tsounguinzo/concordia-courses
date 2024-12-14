import { env } from "$env/dynamic/private";
import { ourFileRouter } from "$lib/server/uploadthing";

import { createRouteHandler } from "uploadthing/server";

const handlers = createRouteHandler({
    router: ourFileRouter,
    config: {
        token: env.UPLOADTHING_TOKEN,
    },
});

export { handlers as GET, handlers as POST };
