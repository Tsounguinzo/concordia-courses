<script lang="ts">
    import Transition from "svelte-transition";
    import {createDialog} from "svelte-headlessui";
    import {twMerge} from "tailwind-merge";
    import {darkModeOn} from "$lib/provider/darkmode.js";
    import ReviewForm from "$lib/components/Course/Review/ReviewForm.svelte";
    import type {Review} from "$lib/model/Review";
    import type {Course} from "$lib/model/Course";
    import {Form, Sveltik} from "sveltik/src";
    import {repo} from "$lib/repo";
    import {Toaster, toast} from "svelte-sonner";
    import * as Yup from "yup";
    import type {Writable} from "svelte/store";
    import {XSquare} from "lucide-svelte";

    export let course: Course;
    export let review: Review;
    export let open: Writable<boolean>;
    export let handleSubmit: (res: Response) => void;

    let initialValues = {
        content: review.content,
        instructors: review.instructors,
        rating: review.rating,
        difficulty: review.difficulty,
    };

    const handleClose = () => {
        open.set(false)
       toast.success('Review draft saved.');
    };

    const ReviewSchema = Yup.object().shape({
        content: Yup.string()
            .required('Review content is required')
            .max(3000, 'Must be less than 3000 characters'),
        instructors: Yup.array().min(1, 'At least 1 instructor is required'),
        rating: Yup.number()
            .min(1, 'Rating must be between 1 and 5')
            .max(5, 'Rating must be between 1 and 5')
            .required('Rating is required'),
        difficulty: Yup.number()
            .min(1, 'Difficulty must be between 1 and 5')
            .max(5, 'Difficulty must be between 1 and 5')
            .required('Difficulty is required'),
    });

    let setFieldValue: (field: string, value: any, shouldValidate?: boolean | undefined) => {};

    function reset() {
        initialValues.content = '';
        initialValues.instructors = [];
        initialValues.rating = 0;
        initialValues.difficulty = 0;
    };

    const dialog = createDialog({label: 'Edit'})
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

            <div class='fixed inset-y-0 left-0 w-screen overflow-y-auto'>
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
                                    initialValues={initialValues}
                                    on:submit={async (values, actions) => {
                                        const res = await repo.updateReview(course._id, values);
                                        actions.setSubmitting(false);
                                        open.set(false);
                                        handleSubmit(res);
                                    }}
                                    let:props
                            >
                                <Form>
                                    <ReviewForm
                                            {props}
                                            course={course}
                                            values={initialValues}
                                            setFieldValue={setFieldValue}
                                            resetForm={reset}
                                    />
                                </Form>
                            </Sveltik>
                        </div>
                    </Transition>
                </div>
            </div>
        </div>
    </Transition>
{/if}