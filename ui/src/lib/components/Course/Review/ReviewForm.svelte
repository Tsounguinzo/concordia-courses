<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {Field} from "sveltik/src";
    import FieldLabel from "$lib/components/Course/Review/FieldLabel.svelte";
    import FieldError from "$lib/components/Course/Review/FieldError.svelte";
    import IconRatingInput from "$lib/components/Course/Review/IconRatingInput.svelte";

    export let props;
    export let course: Course;
    export let setFieldValue: (name: string, value: any) => void;

</script>
<div class='flex flex-col'>
    <FieldLabel For='instructors'>Instructor Name</FieldLabel>
    <Field
            on:input={(e) => props.values.instructor = e.target.value}
            on:blur={props.handleBlur}
            value={props.values.instructor}
            id='instructor'
            name='instructor'
            placeholder='Who was your prof...'
            class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
    />
    <FieldError name='instructor'/>
</div>
<div class='flex gap-x-10'>
    <div class='flex flex-col gap-y-1'>
        <FieldLabel For='rating'>Rating</FieldLabel>
        <IconRatingInput
                {setFieldValue}
                on:blur={props.handleBlur}
                rating={props.values.rating}
                name='rating'
                icon='user'
        />
    </div>
    <div class='flex flex-col gap-y-0.5'>
        <FieldLabel For='difficulty'>Difficulty</FieldLabel>
        <IconRatingInput
                {setFieldValue}
                on:blur={props.handleBlur}
                rating={props.values.difficulty}
                name='difficulty'
                icon='flame'
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
        <button on:click|preventDefault={props.handleReset}
                class='w-fit cursor-pointer rounded-md bg-gray-100 px-4 py-2 font-medium text-gray-700 duration-200 hover:bg-gray-200 dark:bg-neutral-700 dark:text-gray-200 dark:hover:bg-neutral-600'
        >
            Discard
        </button>
        <button type='submit' class='ml-auto w-fit rounded-md bg-red-600 px-4 py-2 font-medium text-white transition duration-300 hover:bg-red-800'>
            Submit
        </button>
    </div>
</div>