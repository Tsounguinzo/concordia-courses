<script lang="ts">
    import {ThumbsDown, ThumbsUp} from "lucide-svelte";
    import {twMerge} from "tailwind-merge";
    import {writable} from "svelte/store";
    import type {Writable} from "svelte/store";
    import type {Interaction, InteractionKind} from "$lib/model/Interaction";
    import type {Review} from "$lib/model/Review";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import {spliceCourseCode} from "$lib/utils";
    import {page} from "$app/stores";

    export let review: Review;
    export let interactions: Interaction[];
    export let promptLogin: Writable<boolean>;
    export let updateLikes: (likes: number) => void;

    const user =  $page.data.user;
    const kind = writable<InteractionKind | undefined | null>(undefined);
    let {courseId, userId, likes} = review;

    const getUserInteractionKind = (interactions: Interaction[]): InteractionKind | undefined => {
        const interaction = interactions.find((interaction: Interaction) => interaction.userId === review.userId);
        return interaction?.kind;
    };

    $: if (interactions) {
        kind.set(getUserInteractionKind(interactions))
    }

    // Determine the change in likes based on interaction
    const getLikeChange = (before: InteractionKind | undefined | null, after: InteractionKind | 'remove') => {
        if (after === 'remove') return before === 'like' ? -1 : before === 'dislike' ? 1 : 0;
        if (before === after) return 0;
        return after === 'like' ? (before === 'dislike' ? 2 : 1) : (before === 'like' ? -2 : -1);
    };

    // Update interaction and handle likes/dislikes accordingly
    const updateInteraction = async (interactionKind: InteractionKind | 'remove') => {
        if (!user) {
            displayLoginPrompt();
            return;
        }

        const change = getLikeChange($kind, interactionKind);
        try {
            if (interactionKind === 'remove') {
                await repo.removeInteraction(courseId, userId, user?.id);
            } else {
                await repo.addOrUpdateInteraction(interactionKind, courseId, userId, user?.id);
            }
            updateLikes(review.likes + change);
            likes += change;

            kind.set(interactionKind === 'remove' ? null : interactionKind);

            const actionWord = interactionKind === 'remove' ? 'Removed' : `Successfully ${interactionKind}d`;
            toast.success(`${actionWord} review for ${spliceCourseCode(courseId, ' ')}.`);
        } catch (err) {
            toast.error(err.toString());
        }
    };

    const displayLoginPrompt = () => {
        promptLogin.set(true);
        setTimeout(() => promptLogin.set(false), 3000);
    };

    // Handlers for like and dislike actions
    const handleLike = () => $kind === 'like' ? updateInteraction('remove') : updateInteraction('like');
    const handleDislike = () => $kind === 'dislike' ? updateInteraction('remove') : updateInteraction('dislike');
</script>

<div class='mb-0.5 flex items-center'>
    <button on:click={handleLike} class='flex h-8 w-8 items-center justify-center rounded-md text-gray-700 focus:outline-none dark:text-white'>
        <ThumbsUp
                class={twMerge(
              'h-4 w-4 cursor-pointer stroke-gray-500',
              $kind === 'like' ? 'stroke-blue-600' : ''
            )}
        />
    </button>
    <span class='text-sm font-bold text-gray-700 dark:text-white'>
          {likes}
    </span>
    <button on:click={handleDislike} class='flex h-8 w-8 items-center justify-center rounded-md text-gray-700 focus:outline-none dark:text-white'>
        <ThumbsDown
                class={twMerge(
              'h-4 w-4 cursor-pointer stroke-gray-500',
              $kind === 'dislike' ? 'stroke-blue-600' : ''
            )}
        />
    </button>
</div>