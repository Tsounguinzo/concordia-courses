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