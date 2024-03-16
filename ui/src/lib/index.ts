import {toast} from "svelte-sonner";
import {repo} from "$lib/repo";

export function observeNotification(node, { updateNotification, notification }) {
    const observer = new IntersectionObserver(([entry]) => {
        if (entry.isIntersecting) {
            updateNotification(notification);
        }
    });

    observer.observe(node);

    return {
        destroy() {
            observer.disconnect();
        }
    };
}

export async function handleLogout() {
    toast.promise((await repo.signOut()).json(), {
        loading: 'Signing out...',
        success: (message) => {
            return message;
        },
        error: 'Oops! Try that logout one more time!',
        finally: () => {
            setTimeout(async () => {
                location.reload();
            }, 800);
        }
    });
}