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

    export let review: Review;
    export let interactions: Interaction[];
    export let promptLogin: Writable<boolean>;
    export let updateLikes: (likes: number) => void;

    const user = null; //useAuth();
    const kind = writable<InteractionKind | undefined | null>(undefined);
    const {courseId, userId, likes} = review;

    const getUserInteractionKind = (interactions: Interaction[]): InteractionKind | undefined => {
        const interaction = interactions.find(
            (interaction: Interaction) => interaction.userId === review.userId
        );

        return interaction?.kind;
    };

    $: if (interactions) {
        kind.set(getUserInteractionKind(interactions))
    }

    const interactionToNum = (kind: InteractionKind) => {
        return kind === 'like' ? 1 : -1;
    };

    const getLikeChange = (
        before: InteractionKind | undefined | null,
        after: InteractionKind
    ) => {
        if (!before) return interactionToNum(after);
        if (before === after) return 0;
        return interactionToNum(after) * 2;
    };

    const addInteraction = async (interactionKind: InteractionKind) => {
        try {
            await repo.addInteraction(interactionKind, courseId, userId, user?.id);
            const change = getLikeChange($kind, interactionKind);
            updateLikes(review.likes + change);

            await refreshInteractions();

            toast.success(
                `Successfully ${interactionKind}d review for ${spliceCourseCode(
                    courseId,
                    ' '
                )}.`
            );
        } catch (err: any) {
            toast.error(err.toString());
        }
    };

    const removeInteraction = async () => {
        try {
            await repo.removeInteraction(courseId, userId, user?.id);
            if (!$kind) {
                toast.error("Can't remove interaction that doesn't exist.");
                return;
            }
            updateLikes(review.likes - interactionToNum($kind));

            await refreshInteractions();

            toast.success(
                `Successfully removed interaction for ${spliceCourseCode(
                    courseId,
                    ' '
                )}.`
            );
        } catch (err: any) {
            toast.error(err.toString());
        }
    };

    const refreshInteractions = async () => {
        try {
            const payload = await repo.getInteractions(courseId, userId, user?.id);
            kind.set(payload.kind);
        } catch (err: any) {
            toast.error(err.toString());
        }
    };

    const displayLoginPrompt = () => {
        promptLogin.set(true);
        setTimeout(() => promptLogin.set(false), 3000);
    };

    const handleLike = () => {
        user
            ? $kind === 'like'
                ? removeInteraction()
                : addInteraction('like')
            : displayLoginPrompt();
    };

    const handleDislike = () => {
        user
            ? $kind === 'dislike'
                ? removeInteraction()
                : addInteraction('dislike')
            : displayLoginPrompt();
    };
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