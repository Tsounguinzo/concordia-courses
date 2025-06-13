<script lang="ts">
    import type {Course} from "$lib/model/Course";
    import {Field} from "sveltik/src";
    import FieldLabel from "$lib/components/common/form/FieldLabel.svelte";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import IconRatingInput from "./IconRatingInput.svelte";
    import {type Instructor} from "$lib/model/Instructor";
    import TagsInput from "$lib/components/review/TagsInput.svelte";
    import AutocompleteInput from "$lib/components/review/AutocompleteInput.svelte";
    import Submit from "$lib/components/common/form/Submit.svelte";

    export let props;
    export let course: Course | null = null;
    export let instructor: Instructor | null = null;
    export let setFieldValue: (name: string, value: any) => void;
    export let resetButton: boolean = false;
    export let variant: 'course' | 'instructor' | 'school' = 'course';
    export let isSubmitting: boolean;

    // State for new resource link inputs
    let newLinkUrl: string = '';
    let newLinkDescription: string = '';

    function addResourceLink() {
        if (!newLinkUrl.trim()) return; // Basic validation: URL cannot be empty
        const newLink = { url: newLinkUrl.trim(), description: newLinkDescription.trim() };
        const updatedResourceLinks = [...(props.values.resourceLinks || []), newLink];
        setFieldValue('resourceLinks', updatedResourceLinks);
        newLinkUrl = ''; // Clear input fields
        newLinkDescription = '';
    }

    function removeResourceLink(index: number) {
        const updatedResourceLinks = [...(props.values.resourceLinks || [])];
        updatedResourceLinks.splice(index, 1);
        setFieldValue('resourceLinks', updatedResourceLinks);
    }

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
                        For={variant === 'course' ? 'experience' : 'rating'}>{variant === 'course' ? 'Experience' : 'Clarity Rating'}</FieldLabel>
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

    <!-- Resource Links Section -->
    <div class='py-3'>
        <h4 class="text-md font-medium text-gray-700 dark:text-gray-300 mb-2">Useful Resources (Optional)</h4>
        {#if props.values.resourceLinks && props.values.resourceLinks.length > 0}
            {#each props.values.resourceLinks as link, i}
                <div class="flex items-center mb-2 p-2 border border-gray-200 dark:border-neutral-600 rounded-md">
                    <div class="flex-grow">
                        <a href={link.url} target="_blank" rel="noopener noreferrer" class="text-blue-600 dark:text-blue-400 hover:underline break-all text-sm">
                            {link.url}
                        </a>
                        {#if link.description}
                        <p class="text-xs text-gray-500 dark:text-gray-400 break-all">{link.description}</p>
                        {/if}
                    </div>
                    <button type="button" on:click={() => removeResourceLink(i)} class="btn btn-error btn-sm ml-2">Remove</button>
                </div>
            {/each}
        {/if}
        <div class="flex items-center mt-2">
            <input type="url" placeholder="New Resource URL (e.g., https://example.com)" bind:value={newLinkUrl} class="input input-bordered w-full mr-2 text-sm" />
            <input type="text" placeholder="Short Description (Optional)" bind:value={newLinkDescription} class="input input-bordered w-full mr-2 text-sm" />
            <button type="button" on:click={addResourceLink} class="btn btn-primary btn-sm whitespace-nowrap">Add Link</button>
        </div>
    </div>
    <!-- End of Resource Links Section -->

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
            <Submit {isSubmitting}/>
        </div>
    </div>
</div>