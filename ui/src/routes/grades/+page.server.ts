import { put } from '@vercel/blob'
import {error} from "@sveltejs/kit";

export const actions = {
    upload: async ({ request }) => {
        try {
            const form = await request.formData();
            const term = form.get('term') as string;
            const year = form.get('year') as string;
            const gradeDistributionFile = form.get('gradeDistribution') as File;
            const classAveragesFile = form.get('classAverages') as File;

            if (!gradeDistributionFile || !classAveragesFile) {
                error(400, { message: 'Both files must be uploaded' })
            }

            const [gradeDistributionBlob, classAveragesBlob] = await Promise.all([
                put(`submissions/${term}-${year}-gradeDistribution.${gradeDistributionFile.name.split('.').pop()}`, gradeDistributionFile, {
                    access: 'public',
                    contentType: gradeDistributionFile.type,
                }),
                put(`submissions/${term}-${year}-classAverages.${classAveragesFile.name.split('.').pop()}`, classAveragesFile, {
                    access: 'public',
                    contentType: classAveragesFile.type,
                }),
            ]);

            return {
                gradeDistributionUrl: gradeDistributionBlob.downloadUrl,
                classAveragesUrl: classAveragesBlob.downloadUrl,
            }
        } catch (err) {
            console.log(err);
            error(500, { message: err.message });
        }
    },
}