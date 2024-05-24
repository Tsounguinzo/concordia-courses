import { put } from '@vercel/blob'
import {error} from "@sveltejs/kit";

export const actions = {
    upload: async ({ request }) => {
        try {
            const form = await request.formData();
            const term = form.get('term') as string;
            const year = form.get('year') as string;
            const gradeDistributionFile = form.get('gradeDistribution') as File;

            if (!gradeDistributionFile) {
                error(400, { message: 'Grades Distribution is required' })
            }

            const gradeDistributionBlob = await put(`submissions/${term}-${year}-gradeDistribution.${gradeDistributionFile.name.split('.').pop()}`, gradeDistributionFile, {
                access: 'public',
                contentType: gradeDistributionFile.type,
            });

            return {
                gradeDistributionUrl: gradeDistributionBlob.downloadUrl,
            }
        } catch (err) {
            console.log(err);
            error(500, { message: err.message });
        }
    },
}