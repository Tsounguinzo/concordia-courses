<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {Field} from "sveltik/src";
    import FieldLabel from "$lib/components/common/form/FieldLabel.svelte";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import IconRatingInput from "./IconRatingInput.svelte";
    import {type Instructor} from "$lib/model/Instructor";
    import TagsInput from "$lib/components/review/TagsInput.svelte";
    import AutocompleteInput from "$lib/components/review/AutocompleteInput.svelte";

    export let props;
    export let course: Course | null = null;
    export let instructor: Instructor | null = null;
    export let setFieldValue: (name: string, value: any) => void;
    export let resetButton: boolean = false;
    export let variant: 'course' | 'instructor' | 'school' = 'course';

</script>
<div style="max-width: calc(100vw - 10rem); max-height: calc(100vh - 10rem);" class='overflow-auto scrollbar-hide'>
    <div class='flex flex-col'>
        {#if variant === 'instructor'}
            <FieldLabel For='course'>Course Name</FieldLabel>
            <AutocompleteInput
                    {setFieldValue}
                    value={props.values.course}
                    options={instructor?.courses.map(c => `${c.subject} ${c.catalog}`) ?? []}
                    name='course'
                    placeholder='What course did you take...'
                    class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white w-full'
            />
            <FieldError name='course'/>
        {:else if variant === 'course'}
            <FieldLabel For='instructor'>Instructor Name</FieldLabel>
            <AutocompleteInput
                    {setFieldValue}
                    value={props.values.instructor}
                    options={course?.instructors ?? []}
                    name='instructor'
                    placeholder='Who was your prof...'
                    class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white w-full'
            />
            <FieldError name='instructor'/>
        {:else if variant === 'school'}
            <FieldLabel For='school'>School Name</FieldLabel>
            <Field
                    disabled={true}
                    on:input={(e) => props.values.school = e.target.value}
                    on:blur={props.handleBlur}
                    value="Concordia University - Montreal"
                    id='school'
                    name='school'
                    placeholder='What school did you attend...'
                    class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
            />
            <FieldError name='school'/>
        {/if}
    </div>
    <div class='flex gap-x-10 flex-wrap'>
        {#if variant === 'course' || variant === 'instructor' }
            <div class='flex flex-col gap-y-1'>
                <FieldLabel
                        For={variant === 'course' ? 'experience' : 'rating'}>{variant === 'course' ? 'Experience' : 'Rating'}</FieldLabel>
                <IconRatingInput
                        {setFieldValue}
                        on:blur={props.handleBlur}
                        rating={variant === 'course' ? props.values.experience : props.values.rating}
                        name={variant === 'course' ? 'experience' : 'rating'}
                        icon='star'
                />
                <FieldError name={variant === 'course' ? 'experience' : 'rating'}/>
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
                <FieldError name='difficulty'/>
            </div>
        {/if}
    </div>
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
    </div>
    <div class='flex flex-col'>
        {#if variant === 'instructor'}
            <FieldLabel For='tags'>Tags (0 min. - 3 max.)</FieldLabel>
            <TagsInput
                    {setFieldValue}
                    selectedTags={props.values.tags}
                    limit={3}
                    name='tags'
            />
            <FieldError name='tags'/>
        {/if}
        <div class='mt-8 flex justify-end space-x-4'>
            <button type="reset"
                    class='w-fit cursor-pointer rounded-md bg-gray-100 px-4 py-2 font-medium text-gray-700 duration-200 hover:bg-gray-200 dark:bg-neutral-700 dark:text-gray-200 dark:hover:bg-neutral-600'
            >
                {resetButton ? "Reset" : "Discard"}
            </button>
            <button type='submit'
                    class='ml-auto w-fit rounded-md bg-blue-600 px-4 py-2 font-medium text-white transition duration-300 hover:bg-blue-800'>
                Submit
            </button>
        </div>
    </div>
</div>