import posthog from 'posthog-js'
import { browser } from '$app/environment';

export const load = async () => {

    if (browser) {
        posthog.init(
            'phc_FxMKJ384BItqvSCo0d6AzRBnEvVQ9qolWYIaKnputvH',
            {
                api_host: 'https://us.i.posthog.com',
                person_profiles: 'always',
            }
        )
    }
    return
};