<script lang="ts">
    import Transition from "svelte-transition";
    import {createDialog} from "svelte-headlessui";
    import {twMerge} from "tailwind-merge";
    import {darkModeOn} from "$lib/darkmode";
    import ReviewForm from "./ReviewForm.svelte";
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";
    import {Sveltik} from "sveltik/src";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import type {Writable} from "svelte/store";
    import Form from "$lib/components/common/form/Form.svelte";

    export let course: Course;
    export let review: Review;
    export let open: Writable<boolean>;
    export let handleSubmit: (res: Response) => void;
    let resetButton = true;

    $: initialValues = {
        content: review.content,
        instructor: review.instructor,
        rating: review.rating,
        difficulty: review.difficulty,
    };

    const handleClose = () => {
        open.set(false)
       toast.success('Review draft saved.');
    };

    const dialog = createDialog({label: 'Edit'})

    const validate = (values, actions) => {
        const errors = {
            content: '',
            instructor: '',
            rating: '',
            difficulty: ''
        };

        if (values.content ==='') {
            errors.content = 'Review content is required';
        } else if (values.instructor == '') {
            errors.instructor = "The instructor's name is required" ;
        } else if (values.rating === 0) {
            errors.rating = "Rating is required" ;
        } else if (values.rating < 1 || values.rating > 5) {
            errors.rating = "Rating must be between 1 and 5" ;
        } else if (values.difficulty === 0) {
            errors.difficulty = "Difficulty is required" ;
        } else if (values.difficulty < 1 || values.difficulty > 5) {
            errors.difficulty = "Difficulty must be between 1 and 5" ;
        }

        return errors;
    };
</script>


{#if $open}
    <Transition appear show={$open} unmount={true}>
        <div class={twMerge('relative z-50', $darkModeOn ? 'dark' : '')}>
            <Transition
                    enter='ease-out duration-200'
                    enterFrom='opacity-0'
                    enterTo='opacity-100'
                    leave='ease-in duration-200'
                    leaveFrom='opacity-100'
                    leaveTo='opacity-0'
            >
                <button class='fixed inset-0 bg-black/25 cursor-default' on:click={handleClose}/>
            </Transition>

            <div class='fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 overflow-y-auto'>
                <div class='flex min-h-full items-center justify-center p-4 text-center'>
                    <Transition
                            enter='ease-out duration-200'
                            enterFrom='opacity-0 scale-95'
                            enterTo='opacity-100 scale-100'
                            leave='ease-in duration-150'
                            leaveFrom='opacity-100 scale-100'
                            leaveTo='opacity-0 scale-95'
                    >
                        <div class='w-full overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all dark:bg-neutral-800'
                             use:dialog.modal>
                            <h3 class='mb-4 text-lg font-medium leading-6 text-gray-900 dark:text-gray-200'>
                                {`Editing review of ${course.subject} ${course.catalog} - ${course.title}`}
                            </h3>

                            <Sveltik
                                    validateOnBlur={false}
                                    validateOnChange={false}
                                    {validate}
                                    {initialValues}
                                    onSubmit={async (values, actions) => {
                                        const res = await repo.updateReview(review, values);
                                        actions.setSubmitting(false);
                                        open.set(false);
                                        handleSubmit(res);
                                    }}
                                    let:props
                                    let:setFieldValue
                            >
                                <Form storageKey="review-form">
                                    <ReviewForm {setFieldValue} {props} {course} {resetButton}/>
                                </Form>
                            </Sveltik>
                        </div>
                    </Transition>
                </div>
            </div>
        </div>
    </Transition>
{/if}