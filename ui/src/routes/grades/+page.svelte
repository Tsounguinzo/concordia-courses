<script>
    import BackgroundBeams from "$lib/components/common/animation/BackgroundBeams.svelte";
    import UploadForm from "$lib/components/grades/UploadForm.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
</script>
<Seo title="Grades Upload" description="Upload your Grades distribution" ogDescription="Upload your Grades distribution" ogTitle="Grades | Concordia.courses" ogImage="og-image-grades.png" ogImageAlt="concordia.courses Grades Upload page Snapshot"/>
<div class='flex w-full justify-center items-center min-h-[calc(100vh-22vh)]'>
    <div class='flex w-full justify-center mx-auto max-w-2xl'>
        <div class='mx-4 flex w-full flex-col rounded-md bg-slate-50 p-6 dark:bg-neutral-800 md:mt-10 relative'>
            <div class='flex flex-col items-center'>
                <h1 class='text-3xl font-semibold text-center text-gray-800 dark:text-gray-200'>
                    Grade Distribution Upload
                </h1>
                <p class='text-gray-600 dark:text-gray-400 text-center'>
                    Thank you!
                </p>
            </div>
            <div class="z-10">
                <UploadForm/>
            </div>
            <BackgroundBeams/>
        </div>
    </div>
</div>