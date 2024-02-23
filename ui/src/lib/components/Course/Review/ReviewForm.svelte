<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {Field} from "sveltik/src";
    import FieldLabel from "$lib/components/Course/Review/FieldLabel.svelte";
    import FieldError from "$lib/components/Course/Review/FieldError.svelte";
    import IconRatingInput from "$lib/components/Course/Review/IconRatingInput.svelte";
    import {writable} from "svelte/store";

    export let props;
    export let course: Course;
    export let setFieldValue: (
        field: string,
        value: any,
        shouldValidate?: boolean | undefined
    ) => void;
    export let values: ReviewFormInitialValues;
    export let resetForm: () => void;

    const instructorNames = Array.from(
        new Set(course.instructors.map((instructor) => instructor.name))
    );

    instructorNames.push('Other');
    type ReviewFormInitialValues = {
        content: string;
        instructors: string[];
        rating: number;
        difficulty: number;
    };
    const instructors = writable(values.instructors);
    const rating = writable(values.rating);
    const difficulty = writable(values.difficulty);
</script>
<div class='flex flex-col'>
    <FieldLabel For='instructors'>Instructor(s) Name</FieldLabel>
    <!--MultiSelect
            className='mt-2'
            options={instructorNames}
            values={instructors}
    /-->
    <Field
            on:input={(e) => props.values.instructors = e.target.value}
            on:change={props.handleChange}
            on:blur={props.handleBlur}
            value={props.values.instructors}
            id='instructors'
            name='instructors'
            placeholder='Who was your prof...'
            class='resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
    />
    <FieldError name='instructors'/>
</div>
<div class='flex gap-x-10'>
    <div class='flex flex-col gap-y-1'>
        <FieldLabel For='rating'>Rating</FieldLabel>
        <IconRatingInput
                name='rating'
                icon='user'
                fieldValue={rating}
        />
    </div>
    <div class='flex flex-col gap-y-0.5'>
        <FieldLabel For='difficulty'>Difficulty</FieldLabel>
        <IconRatingInput
                name='difficulty'
                icon='flame'
                fieldValue={difficulty}
        />
    </div>
</div>
<FieldError name='rating'/>
<FieldError name='difficulty'/>
<div class='py-1'/>
<div class='flex flex-col'>
    <FieldLabel For='content'>Content</FieldLabel>
    <Field
            on:input={(e) => props.values.content = e.target.value}
            on:change={props.handleChange}
            on:blur={props.handleBlur}
            value={props.values.content}
            as='textarea'
            rows="8"
            id='content'
            name='content'
            placeholder='Write your thoughts on this course...'
            class='resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
    />
    <FieldError name='content'/>
    <div class='mt-8 flex justify-end space-x-4'>
        <button on:click|preventDefault={resetForm}
                class='w-fit cursor-pointer rounded-md bg-gray-100 px-4 py-2 font-medium text-gray-700 duration-200 hover:bg-gray-200 dark:bg-neutral-700 dark:text-gray-200 dark:hover:bg-neutral-600'
        >
            Discard
        </button>
        <button
                type='submit'
                class='ml-auto w-fit rounded-md bg-red-600 px-4 py-2 font-medium text-white transition duration-300 hover:bg-red-800'
        >
            Submit
        </button>
    </div>
</div>