<script lang="ts">
    import Transition from "svelte-transition";
    import {twMerge} from "tailwind-merge";
    import {Sveltik} from "sveltik/src";
    import {repo} from "$lib/repo.js";
    import ReviewForm from "./ReviewForm.svelte";
    import {toast} from "svelte-sonner";
    import {darkModeOn} from "$lib/darkmode";
    import type {Course} from "$lib/model/Course";
    import {createDialog} from "svelte-headlessui";
    import type {Writable} from "svelte/store";
    import Form from "$lib/components/common/form/Form.svelte";
    import type {Instructor} from "$lib/model/Instructor";
    import {validateReviewContent, validateFieldPresence, validateNumericRange, validateTags} from "$lib/validators";
    import {visitorId} from "$lib/store";

    export let course: Course | null = null;
    export let instructor: Instructor | null = null;
    export let open: Writable<boolean>;
    export let handleSubmit: (res: Response) => void;
    export let variant: 'course' | 'instructor' | 'school' = 'course';

    let initialValues = {
        content: '',
        instructor: '',
        experience: 0,
        difficulty: 0,
        rating: 0,
        tags: [],
        school: '',
        course: ''
    };

    const handleClose = () => {
        open.set(false)
        toast.success('Review draft saved.');
    };

    const dialog = createDialog({label: 'Add'})

    const validate = (values, actions) => {
        const errors = {
            content: '',
            instructor: '',
            experience: '',
            difficulty: '',
            rating: '',
            tags: '',
            school: '',
            course: ''
        };

        errors.content = validateReviewContent(values.content) || '';
        errors.difficulty = validateNumericRange(values.difficulty, "Difficulty", 1, 5);
        if (variant === 'course'){
            errors.instructor = validateFieldPresence(values.instructor, "Instructor's name");
            errors.experience = validateNumericRange(values.experience, "Experience", 1, 5);
        } else if (variant === 'instructor') {
            errors.course = validateFieldPresence(values.course, "Course name");
            errors.rating = validateNumericRange(values.rating, "Rating", 1, 5);
            errors.tags = validateTags(values.tags);
        } else {
            errors.school = validateFieldPresence(values.school, "School name");
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
                                {#if variant === 'course'}
                                    {`Reviewing ${course?.subject} ${course?.catalog} - ${course?.title}`}
                                {:else if variant === 'instructor'}
                                    {`Reviewing ${instructor?.firstName} ${instructor?.lastName}`}
                                {:else}
                                    Reviewing Concordia University
                                {/if}
                            </h3>
                            <Sveltik
                                    validateOnBlur={false}
                                    validateOnChange={false}
                                    {validate}
                                    {initialValues}
                                    onSubmit={async (values, actions) => {
                                    const res = await repo.addReview(course?._id, instructor?._id, variant, values, $visitorId).finally(() => {
                                        actions.setSubmitting(false)
                                        open.set(false)
                                    });
                                    handleSubmit(res);
                                }}
                                    let:props
                                    let:setFieldValue
                                    let:isSubmitting
                            >
                                <Form storageKey="review-form">
                                    <ReviewForm {setFieldValue} {props} {instructor} {course} {variant} {isSubmitting}/>
                                </Form>
                            </Sveltik>
                        </div>
                    </Transition>
                </div>
            </div>
        </div>
    </Transition>
{/if}